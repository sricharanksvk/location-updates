# location-updates

Using [DefaultLocationClient.kt](app%2Fsrc%2Fmain%2Fjava%2Fcom%2Fsvksricharan%2Ffetchlocation%2FfetchLocation%2FDefaultLocationClient.kt) we can separate the logic of
fetching location in Activity.

In [ContextExt.kt](app%2Fsrc%2Fmain%2Fjava%2Fcom%2Fsvksricharan%2Ffetchlocation%2FContextExt.kt) updateLocation function will get you updates 
after all location permission are allowed, also you can set frequency of updates.

In [MainActivity.kt](app%2Fsrc%2Fmain%2Fjava%2Fcom%2Fsvksricharan%2Ffetchlocation%2FMainActivity.kt) use respective button to start and stop location updates.
Fetch location using fused location.
