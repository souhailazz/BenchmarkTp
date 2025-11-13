package com.projet.rest_benchmark.controller.jersey;

import com.projet.rest_benchmark.dto.ItemDto;
import com.projet.rest_benchmark.service.ItemService;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
@Path("/items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class JerseyItemResource {
    
    private final ItemService itemService;
    
    @GET
    public Response getItems(
            @QueryParam("categoryId") Long categoryId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size) {
        
        Page<ItemDto> items;
        if (categoryId != null) {
            items = itemService.findByCategoryId(categoryId, PageRequest.of(page, size));
        } else {
            items = itemService.findAll(PageRequest.of(page, size));
        }
        return Response.ok(items).build();
    }
    
    @GET
    @Path("/{id}")
    public Response getItemById(@PathParam("id") Long id) {
        ItemDto item = itemService.findById(id);
        return Response.ok(item).build();
    }
    
    @POST
    public Response createItem(@Valid ItemDto itemDto) {
        ItemDto created = itemService.create(itemDto);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }
    
    @PUT
    @Path("/{id}")
    public Response updateItem(@PathParam("id") Long id, @Valid ItemDto itemDto) {
        ItemDto updated = itemService.update(id, itemDto);
        return Response.ok(updated).build();
    }
    
    @DELETE
    @Path("/{id}")
    public Response deleteItem(@PathParam("id") Long id) {
        itemService.delete(id);
        return Response.noContent().build();
    }
}

