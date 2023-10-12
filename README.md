# CurrencyCounterTgBot

The main goal of this project is to create a telegram bot for currency conversioning. To do this, you have to open your Telegram app, find @currencyCounterBot, type /start and follow the bot's instructions

### Notes
- To start this project, you have to provide API keys from your APILayer(https://apilayer.com/marketplace/exchangerates_data-api) and RevAI(https://www.rev.ai/) accounts. Keys should be typed in exchangeratesdata.api.key and revai.api.key fields in src/main/resources/application.properties file
- Application can work only with currencies in ISO 4217 format. You can read more about this here - https://www.iso.org/iso-4217-currency-codes.html

### Prerequisites
- JDK 17
- Docker
- Maven

### Run & Test
General:
1. Download the project
2. Go to the project main directory

To run it using Docker:
1. `docker compose up`

To run it from jar:
1. `docker compose up -d db`
2. `mvn clean install`
3. `java -jar ./target/demo-0.0.1-SNAPSHOT.jar`

To run tests with reports:
1. `docker compose up -d db`
2. `mvn clean test`
