package com.badoo.reaktive.disposable

import kotlin.js.JsName

@Deprecated(
    message = "Use Disposable instead",
    replaceWith = ReplaceWith("Disposable(onDispose)", "com.badoo.reaktive.disposable.Disposable")
)
@JsName("disposableDeprecated")
inline fun disposable(crossinline onDispose: () -> Unit = {}): Disposable = Disposable(onDispose)

        override fun dispose() {
            if (_isDisposed.compareAndSet(false, true)) {
                onDispose()
            }
        }
    }
