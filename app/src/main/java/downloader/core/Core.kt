package downloader.core

class Core private constructor() {
    val executorSupplier: ExecutorSupplier

    companion object {
        private var instance: Core? = null
        @JvmStatic
        fun getInstance(): Core? {
            if (instance == null) {
                synchronized(Core::class.java) {
                    if (instance == null) {
                        instance = Core()
                    }
                }
            }
            return instance
        }

        @JvmStatic
        fun shutDown() {
            if (instance != null) {
                instance = null
            }
        }
    }

    init {
        executorSupplier = DefaultExecutorSupplier()
    }
}