package br.com.sales4you.integrator.mfm.dto

class DemonstrativoContratoOriginal {

    var devedor : Devedor? = null
    var totalOriginal : String? = null
    var totalNegociado : String? = null
    var originalTotal : String? = null
    var valoresContratoOriginal: MutableList<ValorContratoOriginal>? = mutableListOf()
    var parcelaTotalParcela : String? = null
    var parcelaTotalIOF : String? = null
    var parcelaTotal : String? = null
    var parcelaTotalSaldo : String? = null
    var parcelasAcordo: MutableList<ParcelaAcordoCO>? = mutableListOf()
    var tipoNegociacao : String? = null
    var clienteBoleto : String? = null
}