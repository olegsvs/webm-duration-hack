import java.io.File

fun main(args: Array<String>) {
    if (args.isEmpty() || args.getOrNull(1) == null) {
        println("Set file path with -f option")
        return
    }
    val webmDurationStartBytes: ByteArray = "4489".decodeHex()
    val webmZeroDurationBytes: ByteArray = "4489843fe42000".decodeHex()
    val inputFile = File(args[1])
    val inputFileBytes = inputFile.readBytes()
    val indexOfDuration = inputFileBytes.findFirst(webmDurationStartBytes)
    if (indexOfDuration != -1) {
        webmZeroDurationBytes.forEachIndexed { index, byte -> inputFileBytes[indexOfDuration + index] = byte }
        File(inputFile.path.removeSuffix(inputFile.name).plus("out_${inputFile.name}")).writeBytes(inputFileBytes)
        println("Successfully modified and written to the output.webm")
    } else {
        println("Bytes not found")
    }
}

fun ByteArray.findFirst(sequence: ByteArray, startFrom: Int = 0): Int {
    if (sequence.isEmpty()) throw IllegalArgumentException("non-empty byte sequence is required")
    if (startFrom < 0) throw IllegalArgumentException("startFrom must be non-negative")
    var matchOffset = 0
    var start = startFrom
    var offset = startFrom
    while (offset < size) {
        if (this[offset] == sequence[matchOffset]) {
            if (matchOffset++ == 0) start = offset
            if (matchOffset == sequence.size) return start
        } else
            matchOffset = 0
        offset++
    }
    return -1
}

fun String.decodeHex(): ByteArray {
    check(length % 2 == 0) { "Must have an even length" }
    return chunked(2)
        .map { it.toInt(16).toByte() }
        .toByteArray()
}