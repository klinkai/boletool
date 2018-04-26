package br.klink.ai.boletool.dto

import br.com.caelum.stella.boleto.*
import java.math.BigDecimal

class KlinkBoleto : Boleto() {

    init {
        this.comEspecieMoeda("R$")
                .comCodigoEspecieMoeda(9)
                .comAceite(false).comEspecieDocumento("DV")
    }

    var linhaDigitavelPersonalizada = ""

    var codigodeBarrasPersonalizado = ""

    var contrato =  ""

    var coverImage = ""

    var logoImage = ""

    fun comImagemCapa(url: String): KlinkBoleto {
        coverImage = url
        return this
    }

    fun comImagemLogo(url: String): KlinkBoleto {
        logoImage = url
        return this
    }

    fun comLinhaDigitavel(linha: String): KlinkBoleto {
        linhaDigitavelPersonalizada = linha
        return this
    }

    fun comCodigoBarras(codigoBarras: String): KlinkBoleto {
        codigodeBarrasPersonalizado = codigoBarras
        return this
    }

    override fun getLinhaDigitavel(): String {
        return linhaDigitavelPersonalizada
    }

    override fun getCodigoDeBarras(): String {
        return codigodeBarrasPersonalizado
    }

    override fun comBanco(banco: Banco?): KlinkBoleto {
        this.banco = banco
        return this
    }

    override fun comDatas(datas: Datas?): KlinkBoleto {
        this.datas = datas
        return this
    }

    override fun comBeneficiario(beneficiario: Beneficiario?): KlinkBoleto {
        this.beneficiario = beneficiario
        return this
    }

    override fun comPagador(pagador: Pagador?): KlinkBoleto {
        this.pagador = pagador
        return this
    }

    override fun comValorBoleto(valor: BigDecimal?): KlinkBoleto {
        this.valorBoleto = valor
        return this
    }

    override fun comNumeroDoDocumento(numeroDocumento: String?): KlinkBoleto {
        this.numeroDocumento = numeroDocumento
        return this
    }

}