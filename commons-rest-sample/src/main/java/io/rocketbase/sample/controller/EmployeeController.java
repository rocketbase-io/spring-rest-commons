package io.rocketbase.sample.controller;

import io.rocketbase.commons.controller.AbstractCrudChildController;
import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.sample.converter.PersonConverter;
import io.rocketbase.sample.dto.data.EmployeeData;
import io.rocketbase.sample.dto.edit.EmployeeEdit;
import io.rocketbase.sample.model.Company;
import io.rocketbase.sample.model.Employee;
import io.rocketbase.sample.repository.CompanyRepository;
import io.rocketbase.sample.repository.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@Slf4j
@RestController
@RequestMapping("/api/company/{parentId}/person")
public class EmployeeController extends AbstractCrudChildController<Employee, EmployeeData, EmployeeEdit, String, PersonConverter> {

    @Resource
    private CompanyRepository companyRepository;

    @Autowired
    public EmployeeController(EmployeeRepository repository, PersonConverter converter) {
        super(repository, converter);
    }

    @Override
    protected EmployeeRepository getRepository() {
        return (EmployeeRepository) super.getRepository();
    }

    @Override
    protected Employee getEntity(String parentId, String id) {
        Employee entity = getRepository().findOneByCompanyIdAndId(parentId, id);
        if (entity == null) {
            throw new NotFoundException();
        }
        return entity;
    }

    @Override
    protected Page<Employee> findAllByParentId(String parentId, PageRequest pageRequest) {
        return getRepository().findAllByCompanyId(parentId, pageRequest);
    }

    @Override
    protected Employee newEntity(String parentId, EmployeeEdit editData) {
        Company company = companyRepository.findOne(parentId);
        if (company == null) {
            throw new NotFoundException();
        }
        Employee entity = getConverter().newEntity(editData);
        entity.setCompany(company);
        return entity;
    }
}