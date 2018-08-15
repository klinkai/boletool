package br.com.sales4you.integrator.mfm.dto

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement

@XmlAccessorType(XmlAccessType.FIELD)
class ParcelaAcordoCO {

    @XmlElement(name = "NumeroParcela")
    var numeroParcela : String? = null
    @XmlElement(name = "DataVencimento")
    var dataVencimento : String? = null
    @XmlElement(name = "ValorParcela")
    var valorParcela : String? = null
    @XmlElement(name = "StatusParcelaAcordo")
    var statusParcelaAcordo : String? = null
    @XmlElement(name = "IOF_TAC_Boleto")
    var iofTacBoleto : String? = null
    @XmlElement(name = "total")
    var total : String? = null
    @XmlElement(name = "saldo")
    var saldo : String? = null

}