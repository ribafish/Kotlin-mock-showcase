# Kotlin mock showcase project

This super simple project showcases a simple display of a list from an RESTful endpoint, hosted on [mocky.io](https://www.mocky.io/).

The project is developed using the MVVM architecture and written in kotlin. Coroutines are used to trigger a HTTP GET call via the retrofit2 library, with the results shown on the UI (in a RecyclerView) using LiveData. Images are loaded using the Picasso library on demand.

A unit test is developed for the MainViewModel, as that is the class that holds the business logic of this simple app.