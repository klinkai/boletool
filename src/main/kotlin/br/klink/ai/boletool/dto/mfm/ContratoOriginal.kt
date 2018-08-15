package br.com.sales4you.integrator.mfm.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

class ContratoOriginal {

    var codigo: String? = null
    var data: String? = null
    var demonstrativo : DemonstrativoContratoOriginal? = null
}