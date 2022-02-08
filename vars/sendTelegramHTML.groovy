def call(message) {
    def encodedMessage = URLEncoder.encode(message, "UTF-8")

    withCredentials([string(credentialsId: "telegramToken", variable: "TOKEN"), string(credentialsId: "telegramChatId", variable: "CHAT_ID")]) {

        response = httpRequest (consoleLogResponseBody: true,
                contentType: 'APPLICATION_JSON',
                httpMode: 'POST',
                requestBody: "parse_mode=HTML&text=${encodedMessage}&chat_id=${CHAT_ID}",
                url: "https://api.telegram.org/bot${TOKEN}/sendMessage",
                validResponseCodes: '200')
        return response
    }
}