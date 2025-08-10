package com.github.stephenwanjala.crspcalckmp.dataSource

import com.github.stephenwanjala.crspcalckmp.domain.models.Motorcycle
import com.github.stephenwanjala.crspcalckmp.domain.models.Vehicle
import crspcalckmp.composeapp.generated.resources.Res
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import net.codinux.csv.reader.CommentStrategy
import net.codinux.csv.reader.CsvReader

object CRSPDataSource {
    @OptIn(ExperimentalSerializationApi::class)
    private fun loadVehicles(csvData: String): List<Vehicle> {
        return CsvReader(
            fieldSeparator = ',',
            hasHeaderRow = true,
            quoteCharacter = '\"',
            commentCharacter = '#',
            commentStrategy = CommentStrategy.NONE,
            skipEmptyRows = true,
            reuseRowInstance = false,
            ignoreColumns = emptySet(),
            errorOnDifferentFieldCount = false,
            ignoreInvalidQuoteChars = false
        )
            .read(csvData)
            .map { row ->
                Vehicle(
                    bodyType = row["BodyType"] ?: "",
                    crsp = row["CRSP"]?.replace(",", "")?.toDoubleOrNull(),
                    driveConfiguration = row["DriveConfiguration"] ?: "",
                    engineCapacity = row["EngineCapacity"] ?: "",
                    fuel = row["Fuel"] ?: "",
                    gvw = row["GVW"]?.replace(",", "")?.toIntOrNull(),
                    make = row["Make"] ?: "",
                    model = row["Model"] ?: "",
                    modelNumber = row["ModelNumber"] ?: "",
                    seating = row["Seating"]?.toIntOrNull(),
                    transmission = row["Transmission"] ?: ""
                )
            }
    }

    suspend fun loadVehiclesFromResources(): List<Vehicle> {
        val csvText = Res.readBytes("files/vehicles.csv")
            .decodeToString()
        return loadVehicles(csvText)
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun loadMotorcycles(csvData: String): List<Motorcycle> {
        return CsvReader(
            fieldSeparator = ',',
            hasHeaderRow = true,
            quoteCharacter = '\"',
            commentCharacter = '#',
            commentStrategy = CommentStrategy.NONE,
            skipEmptyRows = true,
            reuseRowInstance = false,
            ignoreColumns = emptySet(),
            errorOnDifferentFieldCount = false,
            ignoreInvalidQuoteChars = false
        )
            .read(csvData)
            .map { row ->
                Motorcycle(
                    crsp = row["CRSP"]?.replace(",", "")?.toDoubleOrNull(),
                    engineCapacity = row["EngineCapacity"]?.toIntOrNull(),
                    fuel = row["Fuel"] ?: "",
                    make = row["Make"] ?: "",
                    model = row["Model"] ?: "",
                    modelNumber = row["ModelNumber"] ?: "",
                    transmission = row["Transmission"] ?: "",
                    seating = row["Seating"]?.toIntOrNull()
                )
            }
    }

    suspend fun loadMotorcyclesFromResources(): List<Motorcycle> {
        val csvText = Res.readBytes("files/motorcycles.csv")
            .decodeToString()
        return loadMotorcycles(csvText)
    }
}