package ru.barabo.db.annotation

@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY, AnnotationTarget.PROPERTY_GETTER)
@Retention(AnnotationRetention.RUNTIME)
annotation class ManyToOne (val prefixColumns :String)