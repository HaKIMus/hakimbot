package com.hakim.hakimbot.gigabrain

import com.github.kittinunf.fuel.Fuel
import org.json.JSONObject


class GPTFacade(private val gptToken: String) {
    suspend fun askGPT(question: Question): String {
        val x = Fuel.post("https://api.openai.com/v1/completions").header(
            mapOf(
                "Content-Type" to "application/json",
                "Authorization" to "Bearer $gptToken",
            )
        )
            .body("{\"model\": \"text-davinci-003\", \"prompt\": \"${question.value}\", \"temperature\": 0, \"max_tokens\": 2048, \"echo\": true}")
            .timeout(80 * 1000)
            .responseString()

        return JSONObject(x.third.component1()).getJSONArray("choices").getJSONObject(0).getString("text")
    }
}