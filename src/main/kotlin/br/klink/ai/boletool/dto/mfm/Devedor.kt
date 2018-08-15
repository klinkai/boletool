package br.com.sales4you.integrator.mfm.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import javax.xml.bind.annotation.*

@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
class Devedor {
    @XmlElement(name = "Contrato")
    var contrato: String? = null
    @XmlElement(name = "Credor")
    var credor: String? = null
    @XmlElement(name = "DividasClaro")
    var dividasClaro: String? = null
    @XmlElement(name = "CodigoDevedor")
    var codigoDevedor: String? = null
    @XmlElement(name = "Nome")
    var nome: String? = null
    @XmlElement(name = "DataNascimento")
    var dataNascimento: String? = null
    @XmlElement(name = "NomePai")
    var nomePai: String? = null
    @XmlElement(name = "NomeMae")
    var nomeMae: String? = null
    @XmlElement(name = "Rg")
    var rg: String? = null
    @XmlElement(name = "Cpf")
    var cpf: String? = null

    @XmlElementWrapper(name = "Enderecos")
    @XmlElement(name = "Endereco")
    var enderecos: MutableList<Endereco>? = mutableListOf()

    @XmlElementWrapper(name = "Telefones")
    @XmlElement(name = "Telefone")
    var telefones: MutableList<Telefone>? = mutableListOf()

    @XmlElementWrapper(name = "Emails")
    @XmlElement(name = "Email")
    var emails: MutableList<Email>? = mutableListOf()
}