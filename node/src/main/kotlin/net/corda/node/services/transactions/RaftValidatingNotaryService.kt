package net.corda.node.services.transactions

import net.corda.core.flows.FlowSession
import net.corda.core.flows.NotaryFlow
import net.corda.core.node.ServiceHub
import net.corda.core.node.services.TrustedAuthorityNotaryService
import java.security.PublicKey

/** A validating notary service operated by a group of mutually trusting parties, uses the Raft algorithm to achieve consensus. */
class RaftValidatingNotaryService(
    override val services: ServiceHub,
    override val notaryIdentityKey: PublicKey,
    override val uniquenessProvider: RaftUniquenessProvider
) : TrustedAuthorityNotaryService() {
    override fun createServiceFlow(otherPartySession: FlowSession): NotaryFlow.Service {
        return ValidatingNotaryFlow(otherPartySession, this)
    }

    override fun start() {
        uniquenessProvider.start()
    }

    override fun stop() {
        uniquenessProvider.stop()
    }
}
