package space.jay.bingle

class Constants {

    interface RequestCode {
        companion object {
            const val GOOGLE_SIGN_IN = 1011
            const val PERMISSION_REQUEST = 1020
            const val IMAGE_FROM_CAMERA = 1021
            const val IMAGE_FROM_ALBUM = 1022
        }
    }

    interface Server {
        companion object {
            const val CONNECTION_SUCCESS = -1
            const val CONNECTION_FAILURE = 0
            const val SERVER_RETRY_MAX_COUNT = 3
        }
    }

    interface Init {
        companion object {
            const val App = "0.0"
            const val INT = 0
            const val STRING = ""
        }
    }

    interface Event {
        companion object {
            const val VERSION = 0
            const val LOGIN_GOOGLE = 1
        }
    }

    interface Version {
        companion object {
            const val IS_WORKING = "isWorking"
            const val APP = "app"
            const val BANNER = "banner"
            const val REDIRECT = "redirect"
        }
    }

    interface Preferences {
        companion object {
            const val UPDATE_SCHEDULE = "updateSchedule"
        }
    }

    interface Intent {
        companion object {
            const val ACTION_PERMISSION_RESULT = "space.jay.we.ACTION_PERMISSION_RESULT"
            const val EXTRA_PERMISSION_RESULT = "EXTRA_PERMISSION_RESULT"

            const val EXTRA_PERMISSION_REQUEST = "EXTRA_PERMISSION_REQUEST"
            const val EXTRA_PERMISSION_ALERT_CONTENT = "EXTRA_PERMISSION_ALERT_CONTENT"
        }
    }
}