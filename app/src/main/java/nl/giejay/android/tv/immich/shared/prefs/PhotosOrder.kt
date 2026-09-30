package nl.giejay.android.tv.immich.shared.prefs

import nl.giejay.android.tv.immich.ImmichApplication
import nl.giejay.android.tv.immich.R
import nl.giejay.android.tv.immich.api.model.Asset
import nl.giejay.android.tv.immich.shared.util.Utils.compareToNullSafe
import java.util.Calendar
import java.util.Date

enum class PhotosOrder(val sort: Comparator<Asset>): EnumWithTitle {
    NEWEST_OLDEST(
        { a1, a2 ->
            (a2.exifInfo?.dateTimeOriginal ?: a2.fileModifiedAt)?.compareToNullSafe(
                a1.exifInfo?.dateTimeOriginal ?: a1.fileModifiedAt
            ) ?: 1
        }
    ) {
        override fun getTitle(): String {
            return ImmichApplication.appContext!!.getString(R.string.order_newest_oldest)
        }
    },
    OLDEST_NEWEST(
        { a1, a2 ->
            (a1.exifInfo?.dateTimeOriginal ?: a1.fileModifiedAt)?.compareToNullSafe(
                a2.exifInfo?.dateTimeOriginal ?: a2.fileModifiedAt
            ) ?: 1
        }

    ) {
        override fun getTitle(): String {
            return ImmichApplication.appContext!!.getString(R.string.order_oldest_newest)
        }
    },
    NEWEST_OLDEST_DAY_OLDEST_NEWEST_TIME(
        { a1, a2 ->
            newest_oldest_day_oldest_newest_time(
                 a2.exifInfo?.dateTimeOriginal ?: a2.fileModifiedAt
                ,a1.exifInfo?.dateTimeOriginal ?: a1.fileModifiedAt)
        }
    ) {
        override fun getTitle(): String {
            return ImmichApplication.appContext!!.getString(R.string.order_newest_oldest_date_oldest_newest_time)
        }
    },;

    companion object {
        fun valueOfSafe(name: String, default: PhotosOrder): PhotosOrder{
            return entries.find { it.toString() == name } ?: default
        }
        fun newest_oldest_day_oldest_newest_time(a1: Date?, a2: Date?): Int {
            if(a1 == null || a2 == null)
                return 1
            val a1cal = invertCalendarTime(a1)
            val a2cal = invertCalendarTime(a2)
            return a1cal.compareTo(a2cal)
        }
        fun invertCalendarTime(d: Date): Date{
            val invertRange: (Int,Int,Int) -> Int = {min: Int, max: Int, value: Int -> max - value + min }
            val cal = Calendar.getInstance().apply { time = d }
            cal.set(Calendar.HOUR,invertRange(0,23,cal.get(Calendar.HOUR)))
            cal.set(Calendar.MINUTE,invertRange(0,59,cal.get(Calendar.MINUTE)))
            cal.set(Calendar.SECOND,invertRange(0,59,cal.get(Calendar.SECOND)))
            return cal.time
        }
    }
}
