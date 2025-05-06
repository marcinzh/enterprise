package enterprise
import turbolift.effects.Reader


type RequestEffect = RequestEffect.type

case object RequestEffect extends Reader[RequestIO]
