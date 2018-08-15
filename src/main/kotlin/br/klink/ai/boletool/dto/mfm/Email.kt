package br.com.sales4you.integrator.mfm.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement

@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
class Email {
    @XmlElement(name = "EnderecoEmail")
    var enderecoEmail : String? = null
}