package com.udacity.asteroidradar.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.udacity.asteroidradar.Asteroid

@Entity
data class DatabaseAsteroid constructor(
    @PrimaryKey
    val id: Long,
    val codename: String,
    val closeApproachDate: String,
    val absoluteMagnitude: Double,
    val estimatedDiameter: Double,
    val relativeVelocity: Double,
    val distanceFromEarth: Double,
    val isPotentiallyHazardous: Boolean
)

fun List<DatabaseAsteroid>.asDomainModel(): List<Asteroid> {
    return map { databaseAsteroid ->
        Asteroid (
            id = databaseAsteroid.id,
            codename = databaseAsteroid.codename,
            closeApproachDate = databaseAsteroid.closeApproachDate,
            absoluteMagnitude = databaseAsteroid.absoluteMagnitude,
            estimatedDiameter = databaseAsteroid.estimatedDiameter,
            relativeVelocity = databaseAsteroid.relativeVelocity,
            distanceFromEarth = databaseAsteroid.distanceFromEarth,
            isPotentiallyHazardous = databaseAsteroid.isPotentiallyHazardous
        )
    }
}