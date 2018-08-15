package br.com.sales4you.integrator.mfm.dto

class Devedor {
    var contrato: String? = null
    var credor: String? = null
    var dividasClaro: String? = null
    var codigoDevedor: String? = null
    var nome: String? = null
    var dataNascimento: String? = null
    var nomePai: String? = null
    var nomeMae: String? = null
    var rg: String? = null
    var cpf: String? = null
    var enderecos: MutableList<Endereco>? = mutableListOf()
    var telefones: MutableList<Telefone>? = mutableListOf()
    var emails: MutableList<Email>? = mutableListOf()
}