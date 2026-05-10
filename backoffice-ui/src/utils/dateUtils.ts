export const formatDateTime = (value?: string | null) => {
  if (!value) {
    return 'n/a';
  }

  return new Intl.DateTimeFormat(undefined, {
    dateStyle: 'medium',
    timeStyle: 'medium'
  }).format(new Date(value));
};
