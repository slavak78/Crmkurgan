package downloader.internal.stream

import java.io.IOException
import kotlin.Throws

interface FileDownloadOutputStream {
    @Throws(IOException::class)
    fun write(b: ByteArray?, off: Int, len: Int)

    @Throws(IOException::class)
    fun flushAndSync()

    @Throws(IOException::class)
    fun close()

    @Throws(IOException::class, IllegalAccessException::class)
    fun seek(offset: Long)

    @Throws(IOException::class, IllegalAccessException::class)
    fun setLength(newLength: Long)
}