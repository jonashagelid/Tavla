
Teknologivalg

MVVM (Model-View-ViewModel)
* Skiller UI og logikk for bedre struktur og testbarhet.
* Siden appen er liten, holder én ViewModel for å unngå unødvendig kompleksitet.

Retrofit (REST API - Stoppesteder)
* Enkel og effektiv håndtering av HTTP-forespørsler.
* Brukes kun for REST-kallet til stoppested-søket.
* Gjør JSON håndtering enkelt gjennom GSON factory.

Funksjonalitet
* Søk etter stoppesteder (Rest API - Retrofit)
* Hente linjer og avganger fra et stoppested (GraphQL API - Apollo)
* Enkel fargekoding for linjer basert på transport metode

Antakelser og forbedringer
* Linjenavn (linjekode) kommer som en del av en string fra API-et
  * Løsning: Splitter string på ":" og velger siste index, denne løsningen treffer ikke alltid og trenger utbedring.


```
main/
│── graphql/                      # GraphQL filer
│   │── schema.json.graphqls      # GraphQL schema (inkluderer en feil i navnet)
│   │── StopPlace.graphql         # GraphQL query
│
│── java/
│   │── com/example/tavla/
│       │── data/                 # Datamodeller
│       │   │── StopPlaceResponse.kt  # Datamodell for Retrofit respons
│       │
│       │── network/              # API-klienter (Retrofit & Apollo)
│       │   │── ApolloClient.kt
│       │   │── GeocoderApi.kt
│       │
│       │── ui/theme/              # UI-tema
│       │
│       │── LinesScreen.kt         # Oversikt over linjer og avganger for et stopp
│       │── MainActivity.kt        # Instansierer ViewModel
│       │── SearchScreen.kt        # Søkeside for stoppesteder
│       │── ViewModel.kt           # Datahåndtering og logikk (kun én ViewModel pga liten app)
```
