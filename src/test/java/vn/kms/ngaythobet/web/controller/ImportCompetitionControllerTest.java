package vn.kms.ngaythobet.web.controller;

import lombok.Value;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import vn.kms.ngaythobet.Application;
import vn.kms.ngaythobet.BaseTest;
import vn.kms.ngaythobet.domain.util.exception.InvalidCompetitionDataException;
import vn.kms.ngaythobet.service.competition.CompetitionImportService;
import vn.kms.ngaythobet.web.form.ImportForm;


import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebAppConfiguration
@ContextConfiguration(classes = Application.class)
public class ImportCompetitionControllerTest extends BaseTest{

    @MockBean
    private CompetitionImportService competitionImportService;

    @Autowired
    private WebApplicationContext appContext;

    protected MockMvc mockMvc;


    @Override
    protected void doStartUp() {
        mockLoginUser("user");
        mockMvc = MockMvcBuilders.webAppContextSetup(appContext).build();
    }

    @Test
    public void importSubmit_Success() throws Exception {
        ImportForm importForm = new ImportForm();
        importForm.setAliasKey("WC200000");
        importForm.setCompetitionId("12123");
        importForm.setCompetitionName("WorldCup");

        when(competitionImportService.doImport(importForm.getCompetitionName(), importForm.getAliasKey(), importForm.getCompetitionId())).thenReturn(new AsyncResult<>(true));

        this.mockMvc.perform(postImportForm(importForm).with(csrf().asHeader()))
            .andExpect(status().isOk())
            .andExpect(view().name("auto-import-competition :: import-info"));

        verify(competitionImportService, times(1)).doImport(any(), any(), any());
        verifyNoMoreInteractions(competitionImportService);
    }

    @Test
    public void importSubmit_Fail_Validate() throws Exception {
        ImportForm importForm = new ImportForm();
        importForm.setAliasKey("WC");
        importForm.setCompetitionId("");
        importForm.setCompetitionName("WC2017");

        when(competitionImportService.doImport(importForm.getCompetitionName(), importForm.getAliasKey(), importForm.getCompetitionId())).thenReturn(new AsyncResult<>(true));

        this.mockMvc.perform(postImportForm(importForm).with(csrf().asHeader()))
            .andExpect(status().isOk())
            .andExpect(model().attributeHasFieldErrors("importForm", "aliasKey"))
            .andExpect(model().attributeHasFieldErrors("importForm", "competitionId"))
            .andExpect(model().attributeHasFieldErrors("importForm", "competitionName"))
            .andExpect(view().name("auto-import-competition :: import-info"));

        verify(competitionImportService, times(0)).doImport(any(), any(), any());
        verifyNoMoreInteractions(competitionImportService);
    }

    @Test
    public void importSubmit_Fail_Exception() throws Exception {
        ImportForm importForm = new ImportForm();
        importForm.setAliasKey("WC23423");
        importForm.setCompetitionId("1231234312");
        importForm.setCompetitionName("HoChiMinh");

        when(competitionImportService.doImport(importForm.getCompetitionName(), importForm.getAliasKey(), importForm.getCompetitionId())).thenReturn(new AsyncResult<>(false));
        doNothing().when(competitionImportService).rollbackWhenInterrupted(importForm.getCompetitionName());

        this.mockMvc.perform(postImportForm(importForm).with(csrf().asHeader()))
            .andExpect(status().isOk())
            .andExpect(view().name("auto-import-competition :: import-info"));

        verify(competitionImportService, times(1)).doImport(any(), any(), any());
        verify(competitionImportService, times(1)).rollbackWhenInterrupted(any());

        verifyNoMoreInteractions(competitionImportService);
    }

    @Test
    public void importSubmit_popup() throws Exception {
        ImportForm importForm = new ImportForm();

        this.mockMvc.perform(getImportPopup(importForm).with(csrf().asHeader()))
            .andExpect(status().isOk())
            .andExpect(view().name("auto-import-competition"))
            .andExpect(model().attribute("importForm", importForm))
            .andExpect(model().attribute("footballToken",   "029812a58dcd467b8ce31b8d88e6059d"));

        verifyNoMoreInteractions(competitionImportService);
    }


    private MockHttpServletRequestBuilder postImportForm(ImportForm importForm) {
        return post("/competitions/auto-import")
            .characterEncoding("UTF-8")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("aliasKey", importForm.getAliasKey())
            .param("competitionName", importForm.getCompetitionName())
            .param("competitionId", importForm.getCompetitionId());
    }

    private MockHttpServletRequestBuilder getImportPopup(ImportForm importForm) {
        return get("/competitions/popup/auto-popup-import");
    }

}
