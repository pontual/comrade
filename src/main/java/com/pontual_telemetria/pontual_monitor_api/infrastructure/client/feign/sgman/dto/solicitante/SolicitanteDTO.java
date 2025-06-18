package com.pontual_telemetria.pontual_monitor_api.infrastructure.client.feign.sgman.dto.solicitante;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitanteDTO {
    private Long id;
    private String idErpEmpresa;
    private String idErpFilial;
    private String nome;
    private String razaoSocial;
    private Integer tipoSolicitante;
    private String cnpj;
    private String cpf;
    private String rg;
    private String idErp;
    private String celular;
    private String telefone;
    private String nomeContato;
    private String email;
    private String endereco;
    private String numero;
    private String bairro;
    private String cep;
    private String complemento;
    private Integer uf;
    private Integer cidade;
    private Integer status;
    private String createdAt;
    private String updatedAt;
}
