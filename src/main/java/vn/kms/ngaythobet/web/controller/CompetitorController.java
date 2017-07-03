package vn.kms.ngaythobet.web.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import vn.kms.ngaythobet.domain.competition.*;
import vn.kms.ngaythobet.service.competition.CompetitorService;
import vn.kms.ngaythobet.service.file.Base64EncodeDecode;
import vn.kms.ngaythobet.service.file.FileService;
import vn.kms.ngaythobet.web.form.CompetitorForm;
import vn.kms.ngaythobet.web.util.DataResponse;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Objects;


@Controller
@RequestMapping("/competitor")
public class CompetitorController {
    private CompetitorService competitorService;
    private CompetitionRepository competitionRepository;
    private FileService fileService;

    @Value("${ngaythobet.competitor-logo}")
    private String competitorDefaultLogo;

    @Autowired
    public CompetitorController(CompetitorService competitorService, CompetitionRepository competitionRepository, FileService fileService) {
        this.competitorService = competitorService;
        this.competitionRepository = competitionRepository;
        this.fileService = fileService;
    }

    @PostMapping("/save")
    @ResponseBody
    public DataResponse<List<ObjectError>> saveCompetitorSubmit(@Valid CompetitorForm competitorForm, BindingResult bindingResult) throws IOException {

        if (bindingResult.hasErrors()) {
            return new DataResponse<>(false,bindingResult.getAllErrors());
        }

        Competitor rawCompetitor = new Competitor();
        BeanUtils.copyProperties(competitorForm, rawCompetitor);
        rawCompetitor.setCompetition(competitionRepository.findOne(competitorForm.getCompetitionId()));

        if (!Objects.isNull(competitorForm.getLogoFile())) {
            String fileName = competitorForm.getName() + "-" + competitorForm.getCompetitionId();
            String urlLogo = fileService.saveLogoCompetitor(competitorForm.getLogoFile(),Base64EncodeDecode.encode(fileName));
            rawCompetitor.setLogo(urlLogo);
        }else {
            if (rawCompetitor.getLogo() == null){
                rawCompetitor.setLogo(competitorDefaultLogo);
            }
        }
        competitorService.saveCompetitor(rawCompetitor);
        return new DataResponse<>(true);
    }

    @RequestMapping(value = "/getList/{aliasKey}", method = RequestMethod.GET)
    public String showCompetitorList(Model model, @PathVariable() String aliasKey) {
        model.addAttribute("competitors",competitorService.findAllByCompetition(aliasKey) );
        return "competitor :: competitorsList";
    }
}
