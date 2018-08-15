package br.klink.ai.boletool.client

import br.com.sales4you.integrator.mfm.dto.ContratoOriginal
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.http.ResponseEntity



@Component
class MFMClient {

    @Value("\${MFM_ENDPOINT}")
    private lateinit var endpoint : String

    fun getDadosValoresContratoOriginal(codParcelaAcordo : String, codAcordo : String, codDevedor : String) : ContratoOriginal? {
        val restTemplate = RestTemplate()
        val url = endpoint + "?codParcelaAcordo=" + codParcelaAcordo + "&numParcelaAcordo=1&codAcordo=" + codAcordo + "&codDevedor=" + codDevedor

        val response = restTemplate.getForEntity(url, ContratoOriginal::class.java)

        return response.body
    }
}