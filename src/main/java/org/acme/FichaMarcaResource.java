package org.acme;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import java.util.Optional;
import java.util.List;

@Path("/fichas-marca")
public class FichaMarcaResource {

    @GET
    public List<FichaMarca> getAll(){
        return FichaMarca.listAll();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") long id){
        return FichaMarca.findByIdOptional(id)
                .map(ficha -> Response.ok(ficha).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @Transactional
    public Response insert(@Valid FichaMarca ficha){
        ficha.persist();
        return Response.status(Response.Status.CREATED).entity(ficha).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") long id){
        boolean deleted = FichaMarca.deleteById(id);
        return deleted ? Response.noContent().build() : Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response update(@PathParam("id") long id, @Valid FichaMarca newFicha){
        Optional<FichaMarca> fichaOpt = FichaMarca.findByIdOptional(id);
        if(fichaOpt.isPresent()){
            FichaMarca ficha = fichaOpt.get();
            ficha.historia = newFicha.historia;
            ficha.fundadores = newFicha.fundadores;
            ficha.premiosConquistados = newFicha.premiosConquistados;
            ficha.persist();
            return Response.ok(ficha).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
