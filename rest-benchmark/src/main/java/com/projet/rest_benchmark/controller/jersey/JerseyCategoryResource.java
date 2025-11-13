package com.projet.rest_benchmark.controller.jersey;

import com.projet.rest_benchmark.dto.CategoryDto;
import com.projet.rest_benchmark.dto.ItemDto;
import com.projet.rest_benchmark.service.CategoryService;
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
@Path("/categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class JerseyCategoryResource {
    
    private final CategoryService categoryService;
    private final ItemService itemService;
    
    @GET
    public Response getCategories(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size) {
        
        Page<CategoryDto> categories = categoryService.findAll(PageRequest.of(page, size));
        return Response.ok(categories).build();
    }
    
    @GET
    @Path("/{id}")
    public Response getCategoryById(@PathParam("id") Long id) {
        CategoryDto category = categoryService.findById(id);
        return Response.ok(category).build();
    }
    
    @GET
    @Path("/{id}/items")
    public Response getCategoryItems(
            @PathParam("id") Long id,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size) {
        
        Page<ItemDto> items = itemService.findByCategoryId(id, PageRequest.of(page, size));
        return Response.ok(items).build();
    }
    
    @POST
    public Response createCategory(@Valid CategoryDto categoryDto) {
        CategoryDto created = categoryService.create(categoryDto);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }
    
    @PUT
    @Path("/{id}")
    public Response updateCategory(@PathParam("id") Long id, @Valid CategoryDto categoryDto) {
        CategoryDto updated = categoryService.update(id, categoryDto);
        return Response.ok(updated).build();
    }
    
    @DELETE
    @Path("/{id}")
    public Response deleteCategory(@PathParam("id") Long id) {
        categoryService.delete(id);
        return Response.noContent().build();
    }
}

