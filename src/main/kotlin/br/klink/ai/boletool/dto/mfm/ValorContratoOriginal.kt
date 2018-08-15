package br.com.sales4you.integrator.mfm.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement

@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
class ValorContratoOriginal {

    @XmlElement(name = "parcela")
    var parcela : String? = null

    @XmlElement(name = "vencimento")
    var vencimento : String? = null

    @XmlElement(name = "original")
    var original : String? = null

    @XmlElement(name = "negociado")
    var negociado : String? = null

    @XmlElement(name = "total")
    var total : String? = null
}