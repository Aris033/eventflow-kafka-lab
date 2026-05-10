export const formatMoney = (value: number) =>
  new Intl.NumberFormat(undefined, {
    style: 'currency',
    currency: 'EUR'
  }).format(value);

export const prettyJson = (payload: string) => {
  try {
    return JSON.stringify(JSON.parse(payload), null, 2);
  } catch {
    return payload;
  }
};
