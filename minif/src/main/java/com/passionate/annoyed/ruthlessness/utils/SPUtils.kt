package com.passionate.annoyed.ruthlessness.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

class SPUtils private constructor(context: Context, fileName: String) {
     val sharedPreferences: SharedPreferences =
        context.applicationContext.getSharedPreferences(fileName, Context.MODE_PRIVATE)
     val gson = Gson()

    companion object {
        private val instances = mutableMapOf<String, SPUtils>()

        @Synchronized
        fun getInstance(context: Context, fileName: String = "sp_utils"): SPUtils {
            return instances[fileName] ?: SPUtils(context.applicationContext, fileName).also {
                instances[fileName] = it
            }
        }
    }

    /**
     * 存储数据
     */
    fun put(key: String, value: Any?) {
        sharedPreferences.edit().apply {
            when (value) {
                null -> remove(key)
                is String -> putString(key, value)
                is Int -> putInt(key, value)
                is Boolean -> putBoolean(key, value)
                is Float -> putFloat(key, value)
                is Long -> putLong(key, value)
                else -> putString(key, gson.toJson(value))
            }
        }.apply()
    }

    /**
     * 获取数据（基本类型）
     */
    @Suppress("UNCHECKED_CAST")
     inline fun <reified T> get(key: String, defaultValue: T): T {
        return when (T::class) {
            String::class -> sharedPreferences.getString(key, defaultValue as? String) as T
            Int::class -> (sharedPreferences.getInt(key, (defaultValue as? Int) ?: 0)) as T
            Boolean::class -> (sharedPreferences.getBoolean(key, (defaultValue as? Boolean) ?: false)) as T
            Float::class -> (sharedPreferences.getFloat(key, (defaultValue as? Float) ?: 0f)) as T
            Long::class -> (sharedPreferences.getLong(key, (defaultValue as? Long) ?: 0L)) as T
            else -> getObject(key, defaultValue)
        }
    }

    /**
     * 获取对象
     */
     inline fun <reified T> getObject(key: String, defaultValue: T): T {
        val json = sharedPreferences.getString(key, null)
        return try {
            json?.let { gson.fromJson(it, T::class.java) } ?: defaultValue
        } catch (e: JsonSyntaxException) {
            defaultValue
        } catch (e: ClassCastException) {
            defaultValue
        }
    }

    /**
     * 检查键是否存在
     */
    fun contains(key: String): Boolean {
        return sharedPreferences.contains(key)
    }

    /**
     * 删除键值对
     */
    fun remove(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }

    /**
     * 清空所有数据
     */
    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}