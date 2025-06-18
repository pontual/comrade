package com.pontual_telemetria.pontual_monitor_api.web.dto.sgman.asset;

import com.pontual_telemetria.pontual_monitor_api.infrastructure.client.feign.sgman.dto.patrimonio.PrevisaoHorasDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SgmanAssetDTO {
    private Long id;
    private String idErp;
    private String descricao;
    private Integer idEmpresa;
    private String idErpEmpresa;
    private String empresa;
    private Integer idFilial;
    private String idErpFilial;
    private String filial;
    private Integer unidade;
    private Integer idUsuario;
    private Integer idSolicitante;
    private String idErpSolicitante;
    private String solicitante;
    private Integer idLocalizacao;
    private String idErpLocalizacao;
    private String localizacao;
    private Integer idCategoria;
    private String categoria;
    private Integer idTipoTec;
    private String tipoTec;
    private String observacao;
    private Integer idMarca;
    private String marca;
    private Integer idModelo;
    private String modelo;
    private String serial;
    private String patrimonio;
    private String tag;
    private String dataMatrix;
    private String detalhes;
    private String idErpFornecedor;
    private Integer idFornecedor;
    private String fornecedor;
    private String danfe;
    private String nota;
    private BigDecimal valorPago;
    private Integer tipoAquisicao;
    private Integer idCentroCusto;
    private String idErpCentroCusto;
    private String centroCusto;
    private LocalDate dataCompra;
    private LocalDate dataBaixa;
    private String motivoBaixa;
    private Integer semCalculoPrevisao;
    private String dataGarantia;
    private Integer status;
    private String dataHoraInclusao;
    private String dataHoraAlteracao;
    private List<String> previsoesHorasRetorno;
    private String situacao;
    private Integer idSituacao;
    private List<PrevisaoHorasDTO> previsaoHoras;
}
