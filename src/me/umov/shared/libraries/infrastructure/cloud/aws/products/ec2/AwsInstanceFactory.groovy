package me.umov.shared.libraries.infrastructure.cloud.aws.products.ec2

import me.umov.shared.libraries.domain.cloud.virtualmachine.Instance
import me.umov.shared.libraries.domain.cloud.virtualmachine.InstanceFactory

class AwsInstanceFactory implements InstanceFactory {

    @Override
    Instance getInstance(String id) {
        return new AwsInstance(id)
    }

}
