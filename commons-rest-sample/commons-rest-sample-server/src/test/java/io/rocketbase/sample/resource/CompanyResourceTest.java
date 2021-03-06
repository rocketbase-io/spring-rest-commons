package io.rocketbase.sample.resource;

import io.rocketbase.commons.dto.ErrorResponse;
import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.exception.BadRequestException;
import io.rocketbase.sample.dto.company.CompanyRead;
import io.rocketbase.sample.dto.company.CompanyWrite;
import io.rocketbase.sample.model.CompanyEntity;
import io.rocketbase.sample.repository.mongo.CompanyRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.AssertionErrors;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CompanyResourceTest {

    @LocalServerPort
    int randomServerPort;

    private CompanyResource companyResource;

    @Resource
    private CompanyRepository companyRepository;

    @BeforeEach
    public void setup() throws Exception {
        companyRepository.deleteAll();
        companyResource = new CompanyResource(String.format("http://localhost:%d", randomServerPort));
    }

    @AfterEach
    public void cleanup() throws Exception {
        companyRepository.deleteAll();
    }

    @Test
    public void shouldGetCompany() throws Exception {
        // given
        CompanyEntity company = companyRepository.save(createDefaultCompany());

        // when
        Optional<CompanyRead> data = companyResource.getById(company.getId());

        // then
        assertCompanySame(company, data.get());
    }

    @Test
    public void shouldNotGetUnknownCompany() throws Exception {
        // given

        // when
        Optional<CompanyRead> data = companyResource.getById("notexisting");

        // then
        assertThat(data, equalTo(Optional.empty()));
    }

    @Test
    public void shouldFindAllCompanys() throws Exception {
        // given
        CompanyEntity Company = companyRepository.save(createDefaultCompany());

        // when
        PageableResult<CompanyRead> result = companyResource.find(0, 10);

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), is(1L));
        assertThat(result.getPage(), is(0));
        assertThat(result.getTotalPages(), is(1));
        assertThat(result.getPageSize(), is(10));
        assertThat(result.getContent(), hasSize(1));
        assertCompanySame(Company,
                result.getContent()
                        .get(0));
    }

    @Test
    public void shouldExecuteAllCompanys() throws Exception {
        // given
        CompanyEntity Company = companyRepository.save(createDefaultCompany());

        Object mock = Mockito.mock(Object.class);

        // when
        companyResource.executeAll(companyData -> {
            mock.hashCode();
        }, 1);

        // then
        verify(mock).hashCode();
    }

    @Test
    public void shouldCreateCompany() throws Exception {
        // given
        CompanyWrite companyWrite = CompanyWrite.builder()
                .name("testcompany")
                .email("test@company.org")
                .url("https://company.org")
                .build();

        // when
        CompanyRead companyRead = companyResource.create(companyWrite);

        // then
        assertThat(companyRead, notNullValue());
        assertThat(companyRead.getId(), notNullValue());

        CompanyEntity Company = companyRepository.findById(companyRead.getId()).get();
        assertCompanySame(Company, companyRead);
    }

    @Test
    public void shouldGetStatusCode201OnCreate() {
        // given
        CompanyWrite companyWrite = CompanyWrite.builder()
                .name("new-create")
                .email("new@company.org")
                .url("https://company.org")
                .build();

        // when
        ResponseEntity<CompanyRead> response = new RestTemplate().exchange(String.format("http://localhost:%d/api/company", randomServerPort),
                HttpMethod.POST,
                new HttpEntity<>(companyWrite),
                CompanyRead.class);

        // then
        assertThat(response, notNullValue());
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
    }

    @Test
    public void shouldNotCreateInvalidCompany() throws Exception {
        // given
        CompanyWrite companyWrite = CompanyWrite.builder()
                .name("testcompany")
                .url("https://company.org")
                .build();

        // when
        try {
            CompanyRead companyRead = companyResource.create(companyWrite);

            // then
            AssertionErrors.fail("should not create invalid company");
        } catch (BadRequestException ex) {
            ErrorResponse errorResponse = ex.getErrorResponse();
            assertThat(errorResponse, notNullValue());
            assertThat(errorResponse.getFields(), hasKey("email"));
            assertThat(errorResponse.getFirstFieldValue("email"), not(emptyString()));
        }

    }

    @Test
    public void shouldUpdateCompany() throws Exception {
        // given
        CompanyEntity company = companyRepository.save(createDefaultCompany());
        CompanyWrite companyWrite = CompanyWrite.builder()
                .name("testcompany all new")
                .email("test@company2.org")
                .url("https://company2.org")
                .build();


        // when
        CompanyRead companyRead = companyResource.update(company.getId(), companyWrite);

        // then
        assertThat(companyRead, notNullValue());
        assertThat(companyRead.getId(), is(company.getId()));

        assertThat(companyRead.getName(), is(companyWrite.getName()));
        assertThat(companyRead.getEmail(), is(companyWrite.getEmail()));
        assertThat(companyRead.getUrl(), is(companyWrite.getUrl()));

        company = companyRepository.findById(company.getId()).get();
        assertCompanySame(company, companyRead);
    }

    @Test
    public void shouldDeleteCompany() throws Exception {
        // given
        CompanyEntity company = companyRepository.save(createDefaultCompany());

        // when
        companyResource.delete(company.getId());

        // then
        assertThat(companyRepository.findById(company.getId()).isPresent(), equalTo(false));
    }

    @Test
    public void shouldSortAsc() {
        // given
        CompanyEntity aCompany = CompanyEntity.builder()
                .name("a-company")
                .email("a@company.org")
                .url("https://a-company.org")
                .build();
        companyRepository.saveAll(Arrays.asList(createDefaultCompany(), aCompany));


        // when
        PageableResult<CompanyRead> result = companyResource.find(PageRequest.of(0, 100, Sort.Direction.ASC, "name"));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), is(2L));
        assertThat(result.getPage(), is(0));
        assertThat(result.getTotalPages(), is(1));
        assertThat(result.getContent(), hasSize(2));
        assertCompanySameWithoutId(aCompany,
                result.getContent()
                        .get(0));
    }

    @Test
    public void shouldSortDesc() {
        // given
        CompanyEntity aCompany = CompanyEntity.builder()
                .name("a-company")
                .email("a@company.org")
                .url("https://a-company.org")
                .build();
        companyRepository.saveAll(Arrays.asList(createDefaultCompany(), aCompany));


        // when
        PageableResult<CompanyRead> result = companyResource.find(PageRequest.of(0, 100, Sort.Direction.DESC, "name"));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), is(2L));
        assertThat(result.getPage(), is(0));
        assertThat(result.getTotalPages(), is(1));
        assertThat(result.getContent(), hasSize(2));
        assertCompanySameWithoutId(createDefaultCompany(),
                result.getContent()
                        .get(0));
    }


    private void assertCompanySame(CompanyEntity company, CompanyRead data) {
        assertCompanySameWithoutId(company, data);
        assertThat(data.getId(), is(company.getId()));
    }

    private void assertCompanySameWithoutId(CompanyEntity company, CompanyRead data) {
        assertThat(data, notNullValue());
        assertThat(data.getName(), is(company.getName()));
        assertThat(data.getEmail(), is(company.getEmail()));
        assertThat(data.getUrl(), is(company.getUrl()));
    }


    private CompanyEntity createDefaultCompany() {
        return CompanyEntity.builder()
                .name("testcompany")
                .email("test@company.org")
                .url("https://company.org")
                .build();
    }

}
