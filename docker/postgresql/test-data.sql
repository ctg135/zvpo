
--Test data for signatures

INSERT INTO public.signatures (id,offset_end,offset_start,remainder_length,updated_at,file_type,first_bytes,remainder_hash,status,threat_name) VALUES
	 ('22997918-59e7-49d3-bbfe-759a09a23e9e'::uuid,1,1,1,'2025-04-04 00:00:00.000','txt','qwe','qwe','ACTUAL','Text Signature 1'),
	 ('6a6657d0-66fa-4898-9102-7757e7d8bc6f'::uuid,2,2,2,'2025-04-03 00:00:00.000','txt','ewq','ewq','DELETED','Text Signature 2');
