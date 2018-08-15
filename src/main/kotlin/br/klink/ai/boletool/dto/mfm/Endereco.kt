package br.com.sales4you.integrator.mfm.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement

@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
class Endereco {
    @XmlElement(name = "CodEndereco")
    var codEndereco: String? = null
    @XmlElement(name = "TipoEndereco")
    var tipoEndereco: String? = null
    @XmlElement(name = "municipio")
    var municipio: String? = null
    @XmlElement(name = "Logradouro")
    var logradouro: String? = null
    @XmlElement(name = "Numero")
    var numero: String? = null
    @XmlElement(name = "Cep")
    var cep: String? = null
    @XmlElement(name = "Bairro")
    var bairro: String? = null
    @XmlElement(name = "Cidade")
    var uf: String? = null
}