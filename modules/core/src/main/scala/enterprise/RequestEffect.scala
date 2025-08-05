package enterprise
import turbolift.effects.ReaderEffect


type RequestEffect = RequestEffect.type

case object RequestEffect extends ReaderEffect[RequestIO]
