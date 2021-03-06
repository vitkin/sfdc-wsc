/*
 * #%L
 * sfdc-wsc-tools
 * %%
 * Copyright (C) 2013 salesforce.com, inc.
 * %%
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 * 
 * 1) Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *    following disclaimer.
 * 
 * 2) Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 *    the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * 3) Neither the name of salesforce.com, inc. nor the names of its contributors may be used to endorse or
 *    promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package com.sforce.ws.tools;

/**
 * ToolsException is thrown when the tools encounter an error.
 *
 * @author http://cheenath
 * @version 1.0
 * @since 1.0  Nov 22, 2005
 */
public class ToolsException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 6129376265141779778L;


    public ToolsException() {
		super();
	}

	public ToolsException(String message, Throwable cause) {
		super(message, cause);
	}

	public ToolsException(String message) {
		super(message);
	}

	public ToolsException(Throwable cause) {
		super(cause);
	}

	@Override
    public String toString() {
        return "Error: " + getMessage();
    }
}
