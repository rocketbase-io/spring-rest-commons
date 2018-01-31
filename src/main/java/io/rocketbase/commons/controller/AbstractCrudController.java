package io.rocketbase.commons.controller;

import io.rocketbase.commons.converter.EntityDataEditConverter;
import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
public abstract class AbstractCrudController<Entity, Data, Edit, ID extends Serializable, Converter extends EntityDataEditConverter<Entity, Data, Edit>> implements BaseController {

    private final PagingAndSortingRepository<Entity, ID> repository;
    private final EntityDataEditConverter<Entity, Data, Edit> converter;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public PageableResult<Data> find(@RequestParam(required = false) MultiValueMap<String, String> params) {
        Page<Entity> entities = repository.findAll(parsePageRequest(params));
        return PageableResult.contentPage(converter.fromEntities(entities.getContent()), entities);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/{id}")
    @ResponseBody
    public Data getById(@PathVariable("id") ID id) {
        Entity entity = getEntity(id);
        return converter.fromEntity(entity);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = APPLICATION_JSON_VALUE)
    @ResponseBody
    public Data create(@RequestBody @NotNull @Validated Edit editData) {
        Entity entity = repository.save(converter.newEntity(editData));
        return converter.fromEntity(entity);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/{id}", consumes = APPLICATION_JSON_VALUE)
    @ResponseBody
    public Data update(@PathVariable ID id, @RequestBody @NotNull @Validated Edit editData) {
        Entity entity = getEntity(id);
        converter.updateEntityFromEdit(editData, entity);
        return converter.fromEntity(entity);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/{id}")
    public void delete(@PathVariable("id") ID id) {
        Entity entity = getEntity(id);
        repository.delete(entity);
    }

    protected Entity getEntity(@PathVariable("id") ID id) {
        Entity entity = repository.findOne(id);
        if (entity == null) {
            throw new NotFoundException();
        }
        return entity;
    }


}
