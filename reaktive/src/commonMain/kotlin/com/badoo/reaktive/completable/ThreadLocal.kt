package com.badoo.reaktive.completable

import com.badoo.reaktive.base.exceptions.CompositeException
import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.plusAssign
import com.badoo.reaktive.utils.handleReaktiveError
import com.badoo.reaktive.utils.isolate.IsolatedReference

/**
 * Prevents the downstream from [freezing](https://kotlinlang.org/docs/native-immutability.html)
 * by saving the observer in a thread local storage.
 *
 * Please refer to the corresponding Readme [section](https://github.com/badoo/Reaktive#thread-local-tricks-to-avoid-freezing).
 */
fun Completable.threadLocal(): Completable =
    completable {
        val disposables = CompositeDisposable()
        it.setDisposable(disposables)
        val emitterRef = IsolatedReference(it)
        disposables += emitterRef

        subscribe(
            object : CompletableObserver {
                override fun onSubscribe(disposable: Disposable) {
                    disposables += disposable
                }

                override fun onComplete() {
                    getEmitter()?.onComplete()
                }

                override fun onError(error: Throwable) {
                    getEmitter(error)?.onError(error)
                }

                private fun getEmitter(existingError: Throwable? = null): CompletableEmitter? =
                    try {
                        emitterRef.getOrThrow()
                    } catch (e: Throwable) {
                        handleReaktiveError(if (existingError == null) e else CompositeException(existingError, e))
                        null
                    }
            }
        )
    }
