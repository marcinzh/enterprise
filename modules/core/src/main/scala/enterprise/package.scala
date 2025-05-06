package enterprise
import turbolift.!!


type Service[U] = Response[U] !! (U & RequestEffect)
