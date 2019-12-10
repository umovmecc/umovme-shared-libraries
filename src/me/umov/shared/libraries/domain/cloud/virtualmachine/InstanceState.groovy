package me.umov.shared.libraries.domain.cloud.virtualmachine

enum InstanceState {
	
    RUNNING("running", "passed"),
    STOPPED("stopped", null),
	TERMINATED("terminated", null),
	PENDING("pending", null),
	SHUTTING_DOWN("shutting-down", null),
	STOPPING("stopping", null),

	private final String state
	private final String status
        
    private InstanceState(String state, String status) {
		this.state = state
		this.status = status
	}

	static InstanceState getInstance(String state, String status) {
        InstanceState instanceStatus = null
		
		values().each{ value ->
			if (value.state == state && value.status == status) {
                instanceStatus = value
            }
        }

		return instanceStatus
    }

}