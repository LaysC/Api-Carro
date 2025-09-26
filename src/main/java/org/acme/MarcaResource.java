package org.acme;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Path("/marcas")
@Tag(name = "Marcas", description = "Operações para gerenciar as marcas de carros")
@Produces(jakarta.ws.rs.core.MediaType.APPLICATION_JSON)
@Consumes(jakarta.ws.rs.core.MediaType.APPLICATION_JSON)
public class MarcaResource {

    @GET
    @Operation(summary = "Listar todas as marcas")
    public List<Marca> listarTodas() {
        return Marca.listAll();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Buscar marca por ID")
    @APIResponse(responseCode = "200", description = "Marca encontrada")
    @APIResponse(responseCode = "404", description = "Marca não encontrada")
    public Response buscarPorId(@PathParam("id") Long id) {
        return Marca.findByIdOptional(id)
                .map(marca -> Response.ok(marca).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @Transactional
    @Operation(summary = "Criar uma nova marca")
    @RequestBody(
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Marca.class),
                    example = "{\n  \"nomeDaMarca\": \"Fiat\",\n  \"nomeCompletoEmpresa\": \"Fiat S.p.A.\",\n  \"dataDeFundacao\": \"1899-07-11\",\n  \"paisDeOrigem\": \"Itália\",\n  \"perfil\": {\n    \"historia\": \"A Fiat foi fundada em 1899 em Turim.\",\n    \"fundadores\": \"Giovanni Agnelli\",\n    \"premiosConquistados\": \"Carro do Ano na Europa\"\n  }\n}"
            )
    )
    @APIResponse(responseCode = "201", description = "Marca criada")
    @APIResponse(responseCode = "400", description = "Requisição inválida ou nome já existe")
    public Response criar(@Valid Marca marca) {
        marca.id = null;
        boolean existe = Marca.find("lower(nomeDaMarca)", marca.nomeDaMarca.toLowerCase()).firstResultOptional().isPresent();
        if (existe) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Já existe uma marca com esse nome: " + marca.nomeDaMarca)
                    .build();
        }
        marca.persist();
        return Response.status(Response.Status.CREATED).entity(marca).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Atualizar uma marca existente")
    @APIResponse(responseCode = "200", description = "Marca atualizada")
    @APIResponse(responseCode = "404", description = "Marca não encontrada")
    @APIResponse(responseCode = "400", description = "Nome já existente")
    public Response atualizar(@PathParam("id") Long id, @Valid Marca marcaAtualizada) {
        Optional<Marca> marcaOpt = Marca.findByIdOptional(id);
        if (marcaOpt.isPresent()) {
            Marca marca = marcaOpt.get();
            boolean existe = Marca.find("lower(nomeDaMarca) = ?1 and id != ?2",
                    marcaAtualizada.nomeDaMarca.toLowerCase(), id).firstResultOptional().isPresent();
            if (existe) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Já existe uma marca com esse nome: " + marcaAtualizada.nomeDaMarca)
                        .build();
            }
            marca.nomeDaMarca = marcaAtualizada.nomeDaMarca;
            // Outros campos devem ser atualizados aqui se necessário
            marca.persist();
            return Response.ok(marca).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("/buscar")
    @Operation(summary = "Buscar marcas pelo nome")
    public Response buscarPorNome(@QueryParam("nome") String nome) {
        if (nome == null || nome.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("O parâmetro 'nome' é obrigatório.")
                    .build();
        }
        List<Marca> marcas = Marca.list("lower(nomeDaMarca) LIKE ?1", "%" + nome.toLowerCase() + "%");
        return Response.ok(marcas).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Deletar uma marca")
    @APIResponse(responseCode = "204", description = "Marca deletada")
    @APIResponse(responseCode = "404", description = "Marca não encontrada")
    public Response deletar(@PathParam("id") Long id) {
        boolean deletado = Marca.deleteById(id);
        if (deletado) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Operation(
            summary = "Retorna as marcas conforme o sistema de pesquisa (search)",
            description = "Retorna uma lista de marcas filtrada conforme a pesquisa por padrão no formato JSON"
    )
    @APIResponse(
            responseCode = "200",
            description = "Item retornado com sucesso"
    )
    @Path("/search")
    public Response search(
            @Parameter(description = "Query de busca por nome, país de origem ou nome artístico")
            @QueryParam("q") String q,
            @Parameter(description = "Campo de ordenação da lista de retorno")
            @QueryParam("sort") @DefaultValue("id") String sort,
            @Parameter(description = "Esquema de filtragem de marcas por ordem crescente ou decrescente")
            @QueryParam("direction") @DefaultValue("asc") String direction,
            @Parameter(description = "Define qual página será retornada na response")
            @QueryParam("page") @DefaultValue("0") int page,
            @Parameter(description = "Define quantos objetos serão retornados por query")
            @QueryParam("size") @DefaultValue("4") int size
    ){
        Set<String> allowed = Set.of("id", "nomeDaMarca", "paisDeOrigem", "dataDeFundacao");
        if(!allowed.contains(sort)){
            sort = "id";
        }

        Sort sortObj = Sort.by(
                sort,
                "desc".equalsIgnoreCase(direction) ? Sort.Direction.Descending : Sort.Direction.Ascending
        );

        int effectivePage = Math.max(page, 0);

        PanacheQuery<Marca> query;

        if (q == null || q.isBlank()) {
            query = Marca.findAll(sortObj);
        } else {
            query = Marca.find(
                    "lower(nomeDaMarca) like ?1 or lower(paisDeOrigem) like ?1", sortObj, "%" + q.toLowerCase() + "%");
        }

        List<Marca> marcas = query.page(effectivePage, size).list();

        var response = new SearchMarcaResponse();
        response.Marcas = marcas;
        response.TotalMarcas = query.list().size();
        response.TotalPages = query.pageCount();
        response.HasMore = effectivePage < query.pageCount() - 1;
        response.NextPage = response.HasMore ? "http://localhost:8080/marcas/search?q="+(q != null ? q : "")+"&page="+(effectivePage + 1) + (size > 0 ? "&size="+size : "") : "";

        return Response.ok(response).build();
    }
}
