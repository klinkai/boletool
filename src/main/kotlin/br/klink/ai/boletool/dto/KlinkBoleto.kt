package br.klink.ai.boletool.dto

import br.com.caelum.stella.boleto.*
import br.com.sales4you.integrator.mfm.dto.ContratoOriginal
import java.math.BigDecimal
import java.time.LocalDate

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

    var codigoDevedor = ""

    var credor = ""

    var nrParcela = ""

    var contratoOriginal = ContratoOriginal()

    var dtAcordo = ""

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

    fun comCodigoDevedor(codigoDevedor : String) : KlinkBoleto {
        this.codigoDevedor = codigoDevedor
        return this
    }

    fun comCredor(credor : String) : KlinkBoleto {
        this.credor = credor
        return this
    }

    fun comNrParcela(nrParcela : String) : KlinkBoleto {
        this.nrParcela = nrParcela
        return this
    }

    fun comContratoOriginal(contratoOriginal: ContratoOriginal) : KlinkBoleto {
        this.contratoOriginal = contratoOriginal
        return this
    }

    fun comDtAcordo(dtAcordo : String) : KlinkBoleto {
        this.dtAcordo = dtAcordo
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