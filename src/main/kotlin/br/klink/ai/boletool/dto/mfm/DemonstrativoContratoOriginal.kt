package br.com.sales4you.integrator.mfm.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlElementWrapper

@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
class DemonstrativoContratoOriginal {

    @XmlElement(name = "devedor")
    var devedor : Devedor? = null

    @XmlElement(name = "totalOriginal")
    var totalOriginal : String? = null

    @XmlElement(name = "totalNegociado")
    var totalNegociado : String? = null

    @XmlElement(name = "OriginalTotal")
    var originalTotal : String? = null

    @XmlElementWrapper(name = "valoresContratoOriginal")
    @XmlElement(name = "ValorContratoOriginal")
    var valoresContratoOriginal: MutableList<ValorContratoOriginal>? = mutableListOf()

    @XmlElement(name = "parcelaTotalParcela")
    var parcelaTotalParcela : String? = null

    @XmlElement(name = "parcelaTotalIOF")
    var parcelaTotalIOF : String? = null

    @XmlElement(name = "parcelaTotal")
    var parcelaTotal : String? = null

    @XmlElement(name = "parcelaTotalSaldo")
    var parcelaTotalSaldo : String? = null

    @XmlElementWrapper(name = "parcelasAcordo")
    @XmlElement(name = "ParcelaAcordo")
    var parcelasAcordo: MutableList<ParcelaAcordoCO>? = mutableListOf()

    @XmlElement(name = "tipoNegociacao")
    var tipoNegociacao : String? = null

    @XmlElement(name = "clienteBoleto")
    var clienteBoleto : String? = null
}