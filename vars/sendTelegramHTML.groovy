def call(message) {
    def encodedMessage = URLEncoder.encode(message, "UTF-8")

    withCredentials([string(credentialsId: "telegram-token", variable: "TOKEN"), string(credentialsId: "telegram-chatid", variable: "CHAT_ID")]) {

        response = httpRequest (consoleLogResponseBody: true,
                contentType: 'APPLICATION_JSON',
                httpMode: 'POST',
                requestBody: "parse_mode=HTML&text=${encodedMessage}&chat_id=${CHAT_ID}",
                url: "https://api.telegram.org/bot${TOKEN}/sendMessage",
                validResponseCodes: '200')
        return response
    }
}