package com.pontual_telemetria.pontual_monitor_api.application.service;

import com.pontual_telemetria.pontual_monitor_api.application.mapper.SgmanLocationMapper;
import com.pontual_telemetria.pontual_monitor_api.application.mapper.RequesterMapper;
import com.pontual_telemetria.pontual_monitor_api.domain.exception.sgman.SgmanException;
import com.pontual_telemetria.pontual_monitor_api.infrastructure.client.feign.sgman.SgmanClient;
import com.pontual_telemetria.pontual_monitor_api.infrastructure.client.feign.sgman.dto.patrimonio.PatrimonioResponseDTO;
import com.pontual_telemetria.pontual_monitor_api.infrastructure.client.feign.sgman.dto.solicitante.SolicitanteResponseDTO;
import com.pontual_telemetria.pontual_monitor_api.infrastructure.util.Constants;
import com.pontual_telemetria.pontual_monitor_api.web.dto.sgman.location.SgmanLocationDTO;
import com.pontual_telemetria.pontual_monitor_api.web.dto.sgman.requester.SgmanRequesterDTO;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SgmanIntegrationService {

    @Value("${sgman.client.unit}")
    private String sgmanUnit;

    @Value("${sgman.token.solicitante}")
    private String tokenSolicitante;

    @Value("${sgman.token.patrimonio}")
    private String tokenPatrimonio;

    private final SgmanClient sgmanClient;
    private final RequesterApplicationService requesterApplicationService;
    private final LocationApplicationService locationApplicationService;

    final RequesterMapper requesterMapper;
    final SgmanLocationMapper sgmanLocationMapper;

    public List<SgmanRequesterDTO> requestersListAll(){
        SolicitanteResponseDTO sgmanResponse;
        log.info("[GET-REQUESTERS-SGMAN] Iniciada a consulta de solicitantes no SGMAN");
        try {
            sgmanResponse = sgmanClient.listAllSolicitantes(sgmanUnit, tokenSolicitante);
        } catch(FeignException e){
            String errorMessage = e.contentUTF8() != null ? e.contentUTF8() : e.getMessage();
            log.error(Constants.SGMAN_ERROR_MESSAGE + "{}", errorMessage, e);
            throw new SgmanException(errorMessage);
        }
        List<SgmanRequesterDTO> requesters = requesterMapper.toResponseList(sgmanResponse.getResultList());
        requesterApplicationService.updateRequestsBySgman(requesters);
        log.info("[GET-REQUESTERS-SGMAN] Finalizada a consulta de solicitantes no SGMAN");
        return requesters;
    }

    public List<SgmanLocationDTO> locationListAll(){
        PatrimonioResponseDTO sgmanResponse;
        log.info("[GET-LOCATIONS-SGMAN] Iniciada a consulta de patrimônios no SGMAN");
        try {
            sgmanResponse = sgmanClient.listAllPatrimonios(sgmanUnit, tokenPatrimonio);
        } catch(FeignException e){
            String errorMessage = e.contentUTF8() != null ? e.contentUTF8() : e.getMessage();
            log.error(Constants.SGMAN_ERROR_MESSAGE + "{}", errorMessage, e);
            throw new SgmanException(errorMessage);
        }
        List<SgmanLocationDTO> locations = sgmanLocationMapper.toResponseList(sgmanResponse.getResultList());
        log.info("[GET-LOCATIONS-SGMAN] Finalizada a consulta de patrimônios no SGMAN");
        return locations;
    }

    public List<SgmanLocationDTO> findLocationByRequesterId(Integer requesterId){
        PatrimonioResponseDTO sgmanResponse;
        log.info("Iniciada a consulta de patrimônios por id de solicitante no SGMAN: idSolicitante={}", requesterId);
        try {
            sgmanResponse = sgmanClient.listPatrimonioBySolicitante(requesterId, sgmanUnit, tokenPatrimonio);
        } catch (FeignException e){
            String errorMessage = e.contentUTF8() != null ? e.contentUTF8() : e.getMessage();
            log.error(Constants.SGMAN_ERROR_MESSAGE + "{}", errorMessage, e);
            throw new SgmanException(errorMessage);
        }
        List<SgmanLocationDTO> locations = sgmanLocationMapper.toResponseList(sgmanResponse.getResultList());
        locationApplicationService.updateLocationBySgman(locations);
        log.info("Finalizada a consulta de patrimônios por id de solicitante no SGMAN: idSolicitante={}", requesterId);
        return locations;
    }
}
