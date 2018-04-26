package br.klink.ai.boletool.controller

import br.com.caelum.stella.boleto.Beneficiario
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import br.com.caelum.stella.boleto.bancos.BancoDoBrasil
import br.com.caelum.stella.boleto.bancos.Bradesco
import br.com.caelum.stella.boleto.bancos.Caixa
import br.com.caelum.stella.boleto.bancos.Itau
import br.com.caelum.stella.boleto.transformer.GeradorDeBoleto
import br.klink.ai.boletool.dto.KlinkBoleto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.io.PrintWriter
import java.io.StringWriter
import java.math.BigDecimal
import br.com.caelum.stella.boleto.Datas
import br.com.caelum.stella.boleto.Endereco
import br.com.caelum.stella.boleto.Pagador
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.ResponseBody
import java.io.ByteArrayInputStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import javax.xml.ws.Response


@RestController
class BoletoController {

    val mapBanco = hashMapOf(
                "001" to BancoDoBrasil(),
                "104" to Caixa(),
                "237" to Bradesco(),
                "341" to Itau())

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
            @RequestParam("linhadigitavel", required = false) linhaDigitavel: String?,
            @RequestParam("codigobarras", required = false) codigoBarras: String?,
            @RequestParam("banco", required = false) bancoCode: String?,
            @RequestParam("nomebeneficiario", defaultValue = "Klink") nomeBeneficiario: String,
            @RequestParam("nomepagador", required = false) nomePagador: String?,
            @RequestParam("enderecopagador", required = false) enderecoPagador: String?,
            @RequestParam("cpfpagador", required = false) cpfPagador: String?,
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


        linhaDigitavel ?: run {
            return@generate ResponseEntity.badRequest().body(stringToResource("Informe a linha digitavel."))
        }

        codigoBarras?.let {
            if (codigoBarras.length%2!=0) {
                return@generate ResponseEntity.badRequest().body(stringToResource("Codigo de barras invalido ${codigoBarras}. O codigo deve conter um numero par de caracteres."))
            }
        } ?: run {
            return@generate ResponseEntity.badRequest().body(stringToResource("Informe o codigo de barras."))
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


        codigoBeneficiario?.let { beneficiario = beneficiario.comCodigoBeneficiario(codigoBeneficiario) }
        digitoCodigoBeneficiario?.let { beneficiario = beneficiario.comDigitoCodigoBeneficiario(digitoCodigoBeneficiario) }
        numeroConvenio?.let { beneficiario = beneficiario.comNumeroConvenio(numeroConvenio) }
        carteira?.let { beneficiario = beneficiario.comCarteira(carteira) }

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

}