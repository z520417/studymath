package com.mathtrainer.app.data.converter

import androidx.room.TypeConverter
import com.mathtrainer.app.data.entity.Difficulty
import com.mathtrainer.app.data.entity.NumberRange
import com.mathtrainer.app.data.entity.OperationType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

/**
 * Room数据库类型转换器
 */
class Converters {

    private val gson = Gson()

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromOperationType(operationType: OperationType): String {
        return operationType.name
    }

    @TypeConverter
    fun toOperationType(operationType: String): OperationType {
        return OperationType.valueOf(operationType)
    }

    @TypeConverter
    fun fromDifficulty(difficulty: Difficulty): String {
        return difficulty.name
    }

    @TypeConverter
    fun toDifficulty(difficulty: String): Difficulty {
        return Difficulty.valueOf(difficulty)
    }

    @TypeConverter
    fun fromNumberRange(range: NumberRange): String {
        return "${range.min},${range.max}"
    }

    @TypeConverter
    fun toNumberRange(range: String): NumberRange {
        val parts = range.split(",")
        return NumberRange(parts[0].toInt(), parts[1].toInt())
    }

    @TypeConverter
    fun fromOperationTypeSet(operations: Set<OperationType>): String {
        return operations.joinToString(",") { it.name }
    }

    @TypeConverter
    fun toOperationTypeSet(operations: String): Set<OperationType> {
        return if (operations.isEmpty()) {
            emptySet()
        } else {
            operations.split(",").map { OperationType.valueOf(it) }.toSet()
        }
    }

    @TypeConverter
    fun fromOperationTypeList(value: List<OperationType>?): String? {
        return value?.let { gson.toJson(it.map { op -> op.name }) }
    }

    @TypeConverter
    fun toOperationTypeList(value: String?): List<OperationType>? {
        return value?.let {
            val type = object : TypeToken<List<String>>() {}.type
            val names: List<String> = gson.fromJson(it, type)
            names.map { name -> OperationType.valueOf(name) }
        }
    }

    @TypeConverter
    fun fromNumberRangeMap(value: Map<OperationType, NumberRange>?): String? {
        return value?.let {
            val stringMap = it.mapKeys { entry -> entry.key.name }
            gson.toJson(stringMap)
        }
    }

    @TypeConverter
    fun toNumberRangeMap(value: String?): Map<OperationType, NumberRange>? {
        return value?.let {
            val type = object : TypeToken<Map<String, NumberRange>>() {}.type
            val stringMap: Map<String, NumberRange> = gson.fromJson(it, type)
            stringMap.mapKeys { entry -> OperationType.valueOf(entry.key) }
        }
    }
}
