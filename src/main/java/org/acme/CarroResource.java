package org.acme;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Path("/carros")
public class CarroResource {

    @GET
    @Operation(
            summary = "Retorna todos os carros (getAll)",
            description = "Retorna uma lista de carros por padrão no formato JSON"
    )
    @APIResponse(
            responseCode = "200",
            description = "Lista retornada com sucesso",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Carro.class, type = SchemaType.ARRAY)
            )
    )
    public Response getAll(){
        return Response.ok(Carro.listAll()).build();
    }

    @GET
    @Path("{id}")
    @Operation(
            summary = "Retorna um carro pela busca por ID (getById)",
            description = "Retorna um carro específico pela busca de ID colocado na URL no formato JSON por padrão"
    )
    @APIResponse(
            responseCode = "200",
            description = "Item retornado com sucesso",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Carro.class, type = SchemaType.ARRAY)
            )
    )
    @APIResponse(
            responseCode = "404",
            description = "Item não encontrado",
            content = @Content(
                    mediaType = "text/plain",
                    schema = @Schema(implementation = String.class))
    )
    public Response getById(
            @Parameter(description = "Id do carro a ser pesquisado", required = true)
            @PathParam("id") long id){
        Carro entity = Carro.findById(id);
        if(entity == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(entity).build();
    }

    @GET
    @Operation(
            summary = "Retorna os carros conforme o sistema de pesquisa (search)",
            description = "Retorna uma lista de carros filtrada conforme a pesquisa por padrão no formato JSON"
    )
    @APIResponse(
            responseCode = "200",
            description = "Item retornado com sucesso",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Carro.class, type = SchemaType.ARRAY)
            )
    )
    @Path("/search")
    public Response search(
            @Parameter(description = "Query de busca por modelo, ano de fabricação ou cilindradas")
            @QueryParam("q") String q,
            @Parameter(description = "Campo de ordenação da lista de retorno")
            @QueryParam("sort") @DefaultValue("id") String sort,
            @Parameter(description = "Esquema de filtragem de carros por ordem crescente ou decrescente")
            @QueryParam("direction") @DefaultValue("asc") String direction,
            @Parameter(description = "Define qual página será retornada na response")
            @QueryParam("page") @DefaultValue("0") int page,
            @Parameter(description = "Define quantos objetos serão retornados por query")
            @QueryParam("size") @DefaultValue("4") int size
    ){
        Set<String> allowed = Set.of("id", "modelo", "anoFabricacao", "avaliacao", "cilindradas");
        if(!allowed.contains(sort)){
            sort = "id";
        }

        Sort sortObj = Sort.by(
                sort,
                "desc".equalsIgnoreCase(direction) ? Sort.Direction.Descending : Sort.Direction.Ascending
        );

        int effectivePage = Math.max(page, 0);

        PanacheQuery<Carro> query;

        if (q == null || q.isBlank()) {
            query = Carro.findAll(sortObj);
        } else {
            try {
                int numero = Integer.parseInt(q);
                query = Carro.find(
                        "anoFabricacao = ?1 or cilindradas = ?1",
                        sortObj,
                        numero
                );
            } catch (NumberFormatException e) {
                query = Carro.find(
                        "lower(modelo) like ?1",
                        sortObj,
                        "%" + q.toLowerCase() + "%"
                );
            }
        }

        List<Carro> carros = query.page(effectivePage, size).list();

        var response = new SearchCarroResponse();
        response.Carros = carros;
        response.TotalCarros = query.list().size();
        response.TotalPages = query.pageCount();
        response.HasMore = effectivePage < query.pageCount() - 1;
        response.NextPage = response.HasMore ? "http://localhost:8080/carros/search?q="+(q != null ? q : "")+"&page="+(effectivePage + 1) + (size > 0 ? "&size="+size : "") : "";

        return Response.ok(response).build();
    }

    @POST
    @Operation(
            summary = "Adiciona um registro à lista de carros (insert)",
            description = "Adiciona um item à lista de carros por meio de POST e request body JSON"
    )
    @RequestBody(
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Carro.class)
            )
    )
    @APIResponse(
            responseCode = "201",
            description = "Created",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Carro.class))
    )
    @APIResponse(
            responseCode = "400",
            description = "Bad Request",
            content = @Content(
                    mediaType = "text/plain",
                    schema = @Schema(implementation = String.class))
    )
    @Transactional
    public Response insert(@Valid Carro carro){
        carro.id = null;
        if(carro.marca != null && carro.marca.id != null){
            Marca m = Marca.findById(carro.marca.id);
            if(m == null){
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Marca com id " + carro.marca.id + " não existe").build();
            }
            entity.marca = m;
        } else {
            carro.marca = null;
        }

        if(carro.acessorios != null && !carro.acessorios.isEmpty()){
            Set<Acessorio> resolved = new HashSet<>();
            for(Acessorio a : carro.acessorios){
                if(a == null || a.id == 0){
                    continue;
                }
                Acessorio fetched = Acessorio.findById(a.id);
                if(fetched == null){
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("Acessório com id " + a.id + " não existe").build();
                }
                resolved.add(fetched);
            }
            carro.acessorios = resolved;
        } else {
            carro.acessorios = new HashSet<>();
        }

        Carro.persist(carro);
        return Response.status(Response.Status.CREATED).build();
    }

    @DELETE
    @Operation(
            summary = "Remove um registro da lista de carros (delete)",
            description = "Remove um item da lista de carros por meio de Id na URL"
    )
    @APIResponse(
            responseCode = "204",
            description = "Sem conteúdo",
            content = @Content(
                    mediaType = "text/plain",
                    schema = @Schema(implementation = String.class))
    )
    @APIResponse(
            responseCode = "404",
            description = "Item não encontrado",
            content = @Content(
                    mediaType = "text/plain",
                    schema = @Schema(implementation = String.class))
    )
    @Transactional
    @Path("{id}")
    public Response delete(@PathParam("id") long id){
        Carro entity = Carro.findById(id);
        if(entity == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        entity.acessorios.clear();
        entity.persist();

        Carro.deleteById(id);
        return Response.noContent().build();
    }

    @PUT
    @Operation(
            summary = "Altera um registro da lista de carros (update)",
            description = "Edita um item da lista de carros por meio de Id na URL e request body JSON"
    )
    @RequestBody(
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Carro.class)
            )
    )
    @APIResponse(
            responseCode = "200",
            description = "Item editado com sucesso",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Carro.class, type = SchemaType.ARRAY)
            )
    )
    @APIResponse(
            responseCode = "404",
            description = "Item não encontrado",
            content = @Content(
                    mediaType = "text/plain",
                    schema = @Schema(implementation = String.class))
    )
    @Transactional
    @Path("{id}")
    public Response update(@PathParam("id") long id,@Valid Carro newCarro){
        Carro entity = Carro.findById(id);
        if(entity == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        entity.modelo = newCarro.modelo;
        entity.descricao = newCarro.descricao;
        entity.anoFabricacao = newCarro.anoFabricacao;
        entity.avaliacao = newCarro.avaliacao;
        entity.cilindradas = newCarro.cilindradas;

        if(newCarro.marca != null && newCarro.marca.id != null){
            Marca m = Marca.findById(newCarro.marca.id);
            if(m == null){
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Marca com id " + newCarro.marca.id + " não existe").build();
            }
            entity.marca = m;
        } else {
            entity.marca = null;
        }

        if(newCarro.acessorios != null){
            Set<Acessorio> resolved = new HashSet<>();
            for(Acessorio a : newCarro.acessorios){
                if(a == null || a.id == 0) continue;
                Acessorio fetched = Acessorio.findById(a.id);
                if(fetched == null){
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("Acessório com id " + a.id + " não existe").build();
                }
                resolved.add(fetched);
            }
            entity.acessorios = resolved;
        } else {
            entity.acessorios = new HashSet<>();
        }
        
        entity.persist(); // Linha adicionada para persistir as mudanças
        return Response.status(Response.Status.OK).entity(entity).build();
    }
}
