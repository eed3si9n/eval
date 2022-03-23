package com.eed3si9n.eval

private[eval] object Hex:
  def toHex(bytes: Array[Byte]): String =
    val buffer = new StringBuilder(bytes.length * 2)
    for (i <- bytes.indices) {
      val b = bytes(i)
      val bi: Int = if (b < 0) b + 256 else b.toInt
      buffer append toHex((bi >>> 4).asInstanceOf[Byte])
      buffer append toHex((bi & 0x0f).asInstanceOf[Byte])
    }
    buffer.toString

  private def toHex(b: Byte): Char =
    require(b >= 0 && b <= 15, "Byte " + b + " was not between 0 and 15")
    if b < 10 then ('0'.asInstanceOf[Int] + b).asInstanceOf[Char]
    else ('a'.asInstanceOf[Int] + (b - 10)).asInstanceOf[Char]
end Hex
