# App Assistenza

App Android (Kotlin + Jetpack Compose) per tecnici di assistenza: clienti, macchine,
interventi con firma del cliente, report PDF e invio via email/WhatsApp.
I dati restano sul telefono (database Room).

## Struttura
- `model/` – entità del database (Cliente, Macchina, Intervento) e relazioni
- `data/` – DAO, database Room, repository, dati del tecnico
- `ui/` – ViewModel, navigazione, tema, schermate Compose
- `report/` – firma, generazione PDF, condivisione
- `util/` – formattazione di date e importi

## Generare l'APK
Ogni push sul branch `main` avvia la build in **Actions**; l'APK si scarica dall'artifact `app-debug`.
In locale: apri il progetto in Android Studio → Build → Build APK(s).
