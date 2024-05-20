package dev.zio.quickstart

case class CacheItem[K, V](value: V, left: Option[K], right: Option[K])
