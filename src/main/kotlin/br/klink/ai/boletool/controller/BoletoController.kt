package br.klink.ai.boletool.controller

import br.com.caelum.stella.boleto.Beneficiario
import br.com.caelum.stella.boleto.Datas
import br.com.caelum.stella.boleto.Endereco
import br.com.caelum.stella.boleto.Pagador
import br.com.caelum.stella.boleto.bancos.BancoDoBrasil
import br.com.caelum.stella.boleto.bancos.Bradesco
import br.com.caelum.stella.boleto.bancos.Caixa
import br.com.caelum.stella.boleto.bancos.Itau
import br.com.caelum.stella.boleto.transformer.GeradorDeBoleto
import br.klink.ai.boletool.EmailServiceImpl
import br.klink.ai.boletool.client.MFMClient
import br.klink.ai.boletool.dto.BoletoMFMResult
import br.klink.ai.boletool.dto.KlinkBoleto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.io.*
import java.math.BigDecimal
import java.nio.file.StandardCopyOption
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


@RestController
class BoletoController {

    val mapBanco = hashMapOf(
                "001" to BancoDoBrasil(),
                "104" to Caixa(),
                "237" to Bradesco(),
                "341" to Itau())

    @Autowired
    private lateinit var mfmClient: MFMClient

    @Autowired
    private lateinit var emailSender : EmailServiceImpl

    @Value("\${mail.recipient}")
    private lateinit var defaultRecipient : String

    @GetMapping("/")
    fun index() : String {
        return "<html><h1>Boletool - <a href='http://klink.ai'>Klink.ai</a></h1><h3>Geração de boletos simples</h3></html>"
    }

    fun stringToResource(str: String): InputStreamResource {
        return InputStreamResource(ByteArrayInputStream(str.toByteArray()))
    }

    @GetMapping("/gerar")
    fun generate(
            @RequestParam("template", required = false) template: String?,
            @RequestParam("linhadigitavel", required = true) linhaDigitavel: String,
            @RequestParam("codigobarras", required = true) codigoBarras: String,
            @RequestParam("banco", required = false) bancoCode: String?,
            @RequestParam("nomebeneficiario", defaultValue = "Klink") nomeBeneficiario: String,
            @RequestParam("nomepagador", required = false) nomePagador: String?,
            @RequestParam("enderecopagador", required = false) enderecoPagador: String?,
            @RequestParam("cpfpagador", required = false) cpfPagador: String?,
            @RequestParam("contrato", required = false) contrato: String?,
            @RequestParam("agencia", defaultValue = "0000") agencia: String,
            @RequestParam("digitoagencia", defaultValue = "0") digitoAgencia: String,
            @RequestParam("imagemcapa", required = false) imagemCapa: String?,
            @RequestParam("imagemlogo", required = false) imagemLogo: String?,
            @RequestParam("logradourobeneficiario", defaultValue = "R. Funchal, 538") rua: String,
            @RequestParam("bairrobeneficiario", defaultValue = "Vila Olímpia") bairro: String,
            @RequestParam("cepbeneficiario", defaultValue = "00000-000") cep: String,
            @RequestParam("cidadebeneficiario", defaultValue = "São Paulo") cidade: String,
            @RequestParam("ufbeneficiario", defaultValue = "SP") uf: String,
            @RequestParam("codigobeneficiario", required = false) codigoBeneficiario: String?,
            @RequestParam("digitocodigobeneficiario", required = false) digitoCodigoBeneficiario: String?,
            @RequestParam("numeroconvenio", required = false) numeroConvenio: String?,
            @RequestParam("carteira", defaultValue = "00") carteira: String,
            @RequestParam("nossonumero", defaultValue = "0000") nossoNumero: String,
            @RequestParam("digitonossonumero", defaultValue = "0") digitoNossoNumero: String,
            @RequestParam("numerodocumento", defaultValue = "00000") numeroDocumento: String,
            @RequestParam("vencimento", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) vencimento: LocalDate?,
            @RequestParam("valor", required = true) valor: BigDecimal) : ResponseEntity<Resource> {

        var boleto = KlinkBoleto()

        if (codigoBarras.length%2!=0) {
            return@generate ResponseEntity.badRequest()
                    .body(stringToResource("Codigo de barras invalido ${codigoBarras}. O codigo deve conter um numero par de caracteres."))
        }

        val venci = vencimento?: LocalDate.now().plusMonths(1)

        val dateTime = LocalDateTime.now()
        val datas = Datas.novasDatas()
                .comDocumento(dateTime.dayOfMonth, dateTime.monthValue, dateTime.year)
                .comProcessamento(dateTime.dayOfMonth, dateTime.monthValue, dateTime.year)
                .comVencimento(venci.dayOfMonth, venci.monthValue, venci.year)

        boleto = boleto.comDatas(datas)

        val enderecoBeneficiario = Endereco.novoEndereco()
                .comLogradouro(rua)
                .comBairro(bairro)
                .comCep(cep)
                .comCidade(cidade)
                .comUf(uf)

        var beneficiario = Beneficiario.novoBeneficiario()
                .comNomeBeneficiario(nomeBeneficiario)
                .comAgencia(agencia).comDigitoAgencia(digitoAgencia)
                .comEndereco(enderecoBeneficiario)
                .comNossoNumero(nossoNumero)
                .comDigitoNossoNumero(digitoNossoNumero)
                .comCarteira(carteira)


        codigoBeneficiario?.let { beneficiario = beneficiario.comCodigoBeneficiario(codigoBeneficiario) }
        digitoCodigoBeneficiario?.let { beneficiario = beneficiario.comDigitoCodigoBeneficiario(digitoCodigoBeneficiario) }
        numeroConvenio?.let { beneficiario = beneficiario.comNumeroConvenio(numeroConvenio) }

        boleto = boleto.comBeneficiario(beneficiario)

        var pagador = Pagador.novoPagador()

        nomePagador?.let{ pagador = pagador.comNome(nomePagador) }
        cpfPagador?.let{ pagador = pagador.comDocumento(cpfPagador) }

        pagador = pagador.comEndereco(enderecoBeneficiario)
        boleto = boleto.comPagador(pagador)

        val bancoCodeLocal = bancoCode?.let { bancoCode } ?: run { linhaDigitavel?.substring(0, 3) }
        if (mapBanco.containsKey(bancoCodeLocal)) {
            boleto = boleto.comBanco(mapBanco[bancoCodeLocal])
        } else {
            //TODO: change to a structured error
            return@generate ResponseEntity.badRequest().body(stringToResource("Banco inválido: ${bancoCodeLocal}."))
        }


        imagemCapa?.let { boleto = boleto.comImagemCapa(imagemCapa) }
        imagemLogo?.let { boleto = boleto.comImagemLogo(imagemLogo) }
        linhaDigitavel?.let { boleto = boleto.comLinhaDigitavel(linhaDigitavel) }
        codigoBarras?.let { boleto = boleto.comCodigoBarras(codigoBarras) }

        boleto = boleto.comValorBoleto(valor)
                .comNumeroDoDocumento(numeroDocumento)

        boleto.contrato = contrato ?: ""

        val gerador = template?.let {
            try {
                GeradorDeBoleto(ClassPathResource("/templates/boleto-${template}.jasper").inputStream, emptyMap(), boleto)
            } catch (e: Exception) {
                return@generate ResponseEntity.badRequest().body(stringToResource("Template inválido: ${template}."))
            }
        } ?: run {
            GeradorDeBoleto(boleto)
        }

        try {
            val pdfStream = gerador.geraPDFStream()
            val resource = InputStreamResource(pdfStream)
            val filename = UUID.randomUUID().toString() + ".pdf"

            return ResponseEntity.ok()
                    .header("Content-Disposition", "filename=\"${filename}\"\n")
                    .contentType(MediaType.parseMediaType("application/pdf"))
                    .body(resource)
        } catch (e: Exception) {
            val sw = StringWriter()
            e.printStackTrace(PrintWriter(sw))
            return ResponseEntity.badRequest().body(stringToResource(sw.toString()))
        }
    }

    @GetMapping("/gerarMFM")
    fun generateMFM(
            @RequestParam("linhadigitavel", required = true) linhaDigitavel: String,
            @RequestParam("codigobarras", required = true) codigoBarras: String,
            @RequestParam("credor", defaultValue = "NET") credor: String,
            @RequestParam("nomepagador", required = true) nomePagador: String?,
            @RequestParam("logradouropagador", required = true) logradouroPagador: String?,
            @RequestParam("cidadepagador", required = true) cidadePagador: String?,
            @RequestParam("ufpagador", required = true) ufPagador: String?,
            @RequestParam("cepPagador", required = true) cepPagador : String?,
            @RequestParam("documentopagador", required = true) documentoPagador: String?,
            @RequestParam("codigodevedor", required = true) codigoDevedor: String,
            @RequestParam("contrato", required = true) contrato: String?,
            @RequestParam("vencimento", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) vencimento: LocalDate?,
            @RequestParam("valor", required = true) valor: BigDecimal,
            @RequestParam("nrparcela", required = true) nrParcela : String,
            @RequestParam("codparcelaacordo", required = false) codParcelaAcordo : String?,
            @RequestParam("codacordo", required = false) codAcordo : String?,
            @RequestParam("dtacordo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) dtAcordo: LocalDate?,
            @RequestParam("email", required = false) email : String?) : BoletoMFMResult {

        var boleto = KlinkBoleto()
        var pdfStream : InputStream? = null
        var result = BoletoMFMResult()
        boleto.comCredor(credor)
        boleto.comCodigoDevedor(codigoDevedor)
        boleto.comNrParcela(nrParcela)

        val venci = vencimento?: LocalDate.now().plusMonths(1)

        val dateTime = LocalDateTime.now()
        val datas = Datas.novasDatas()
                .comDocumento(dateTime.dayOfMonth, dateTime.monthValue, dateTime.year)
                .comProcessamento(dateTime.dayOfMonth, dateTime.monthValue, dateTime.year)
                .comVencimento(venci.dayOfMonth, venci.monthValue, venci.year)

        boleto = boleto.comDatas(datas)

        dtAcordo?.let {
            var formatters = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            boleto = boleto.comDtAcordo(dtAcordo.format(formatters))
        }

        var pagador = Pagador.novoPagador()

        nomePagador?.let{ pagador = pagador.comNome(nomePagador) }
        documentoPagador?.let{ pagador = pagador.comDocumento(documentoPagador) }

        var endereco = Endereco.novoEndereco()

        logradouroPagador?.let { endereco = endereco.comLogradouro(logradouroPagador)}
        cidadePagador?.let { endereco = endereco.comCidade(cidadePagador) }
        ufPagador?.let { endereco = endereco.comUf(ufPagador) }
        cepPagador?.let { endereco = endereco.comCep(cepPagador) }

        pagador = pagador.comEndereco(endereco)

        boleto = boleto.comPagador(pagador)


        linhaDigitavel?.let { boleto = boleto.comLinhaDigitavel(linhaDigitavel) }
        codigoBarras?.let { boleto = boleto.comCodigoBarras(codigoBarras) }

        boleto = boleto.comValorBoleto(valor)
        boleto.contrato = contrato ?: ""

        try {
            var gerador : GeradorDeBoleto? = null

            if (nrParcela.equals("1")) {

                val contratoOriginal = mfmClient.getDadosValoresContratoOriginal(codParcelaAcordo!!, codAcordo!!, codigoDevedor)

                contratoOriginal?.let {
                    boleto = boleto.comContratoOriginal(contratoOriginal!!)
                }

                gerador = GeradorDeBoleto(ClassPathResource("/templates/boleto-net-mfm-parcela1.jasper").inputStream, emptyMap(), boleto)
            } else {
                gerador = GeradorDeBoleto(ClassPathResource("/templates/boleto-net-mfm.jasper").inputStream, emptyMap(), boleto)
            }

            pdfStream = gerador.geraPDFStream()
            val filename = "Boleto_" + contrato + "_parc_" + nrParcela + ".pdf"


            val targetFile = File("/tmp/" + filename)

            java.nio.file.Files.copy(
                    pdfStream,
                    targetFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);

            val recipient = if (StringUtils.isEmpty(defaultRecipient)) email else defaultRecipient
            val subject = "Boleto NET - Acordo Contrato " + contrato + " - Parcela " + nrParcela
            val text = "Olá, " + nomePagador + "\n\nSegue em anexo o boleto referente à parcela " + nrParcela + " do seu acordo.\n\nAtenciosamente,\nIvonete - Negocia Fácil"

            emailSender.sendSimpleMessage(recipient!!, subject, text, targetFile, filename)

            result.linhaDigitavel = linhaDigitavel
            result.msg = "Boleto enviado para o e-mail " + recipient

        } catch (e: Exception) {
            val sw = StringWriter()
            result.status = HttpStatus.INTERNAL_SERVER_ERROR.value()
            result.msg = e.message
            e.printStackTrace(PrintWriter(sw))
        } finally {
            pdfStream?.close()
        }

        return result
    }
}